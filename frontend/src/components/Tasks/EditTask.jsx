// @ts-check

import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Form, Button } from 'react-bootstrap';
import { useFormik } from 'formik';
import { useParams, useNavigate } from 'react-router-dom';
import * as yup from 'yup';
import axios from 'axios';

import routes from '../../routes.js';
import { useAuth, useNotify } from '../../hooks/index.js';

import getLogger from '../../lib/logger.js';

const log = getLogger('edit task');
log.enabled = true;

const getValidationSchema = () => yup.object().shape({});

const EditTask = () => {
  const { t } = useTranslation();

  const [taskData, setTaskData] = useState({
    task: {},
    executors: [],
    labels: [],
    statuses: [],
  });
  const params = useParams();
  const auth = useAuth();
  const notify = useNotify();
  const navigate = useNavigate();

  useEffect(() => {
    const errorHandler = (e) => {
      if (e.response?.status === 401) {
        const from = { pathname: routes.loginPagePath() };
        navigate(from);
        notify.addErrors([{ defaultMessage: t('Доступ запрещён! Пожалуйста, авторизируйтесь.') }]);
      } else if (e.response?.status === 422 && Array.isArray(e.response?.data)) {
        notify.addErrors(e.response?.data);
      } else {
        notify.addErrors([{ defaultMessage: e.message }]);
      }
    };

    const fetchData = async () => {
      try {
        const [
          { data: currentTaskData },
          { data: executorsData },
          labelsData,
          { data: statusesData },
        ] = await Promise.all([
          axios.get(`${routes.apiTasks()}/${params.taskId}`, { headers: auth.getAuthHeader() }),
          axios.get(routes.apiUsers(), { headers: auth.getAuthHeader() }),
          axios.get(routes.apiLabels(), { headers: auth.getAuthHeader() }).catch(errorHandler),
          axios.get(routes.apiStatuses(), { headers: auth.getAuthHeader() }),
        ]);
        setTaskData({
          task: currentTaskData,
          executors: executorsData,
          labels: labelsData ? labelsData.data : [],
          statuses: statusesData,
        });
      } catch (e) {
        errorHandler(e);
      }
    };
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const {
    task,
    executors,
    labels,
    statuses,
  } = taskData;

  const f = useFormik({
    enableReinitialize: true,
    initialValues: {
      name: task.name,
      description: task.description,
      status: task.taskStatus?.id,
      executor: task.executor?.id,
      labels: task.labels?.map(({ id }) => id),
    },
    validationSchema: getValidationSchema(),
    onSubmit: async (currentTaskData, { setSubmitting, setErrors }) => {
      try {
        const requestTask = {
          name: currentTaskData.name,
          description: currentTaskData.description,
          executorId: parseInt(currentTaskData.executor, 10),
          taskStatusId: parseInt(currentTaskData.status, 10),
          labelIds: currentTaskData.labels.map((id) => parseInt(id, 10)),
        };
        const data = await axios.put(`${routes.apiTasks()}/${task.id}`, requestTask, { headers: auth.getAuthHeader() });
        log('task.edit', data);
        const from = { pathname: routes.tasksPagePath() };
        navigate(from);

        notify.addMessage(t('taskEdited'));
      } catch (e) {
        setSubmitting(false);
        if (e.response?.status === 401) {
          const from = { pathname: routes.loginPagePath() };
          navigate(from);
          notify.addErrors([{ defaultMessage: t('Доступ запрещён! Пожалуйста, авторизируйтесь.') }]);
        } else if (e.response?.status === 422 && Array.isArray(e.response?.data)) {
          const errors = e.response?.data
            .reduce((acc, err) => ({ ...acc, [err.field]: err.defaultMessage }), {});
          setErrors(errors);
        } else {
          notify.addErrors([{ defaultMessage: e.message }]);
        }
      }
    },
    validateOnBlur: false,
    validateOnChange: false,
  });

  return (
    <>
      <h1 className="my-4">{t('taskCreating')}</h1>
      <Form onSubmit={f.handleSubmit}>
        <Form.Group className="mb-3" controlId="name">
          <Form.Label>{t('naming')}</Form.Label>
          <Form.Control
            type="text"
            value={f.values.name}
            disabled={f.isSubmitting}
            onChange={f.handleChange}
            onBlur={f.handleBlur}
            isInvalid={f.errors.name && f.touched.name}
            name="name"
          />
          <Form.Control.Feedback type="invalid">
            {t(f.errors.name)}
          </Form.Control.Feedback>
        </Form.Group>

        <Form.Group className="mb-3" controlId="description">
          <Form.Label>{t('description')}</Form.Label>
          <Form.Control
            as="textarea"
            rows={3}
            value={f.values.description}
            disabled={f.isSubmitting}
            onChange={f.handleChange}
            onBlur={f.handleBlur}
            isInvalid={f.errors.description && f.touched.description}
            name="description"
          />
          <Form.Control.Feedback type="invalid">
            {t(f.errors.description)}
          </Form.Control.Feedback>
        </Form.Group>

        <Form.Group className="mb-3" controlId="status">
          <Form.Label>{t('status')}</Form.Label>
          <Form.Select
            nullable
            value={f.values.status}
            disabled={f.isSubmitting}
            onChange={f.handleChange}
            onBlur={f.handleBlur}
            isInvalid={f.errors.status && f.touched.status}
            name="status"
          >
            <option value="">{null}</option>
            {statuses
              .map((status) => <option key={status.id} value={status.id}>{status.name}</option>)}
          </Form.Select>
          <Form.Control.Feedback type="invalid">
            {t(f.errors.status)}
          </Form.Control.Feedback>
        </Form.Group>

        <Form.Group className="mb-3" controlId="executor">
          <Form.Label>{t('executor')}</Form.Label>
          <Form.Select
            value={f.values.executor}
            disabled={f.isSubmitting}
            onChange={f.handleChange}
            onBlur={f.handleBlur}
            isInvalid={f.errors.executor && f.touched.executor}
            name="executor"
          >
            <option value="">{null}</option>
            {executors
              .map((executor) => <option key={executor.id} value={executor.id}>{`${executor.firstName} ${executor.lastName}`}</option>)}
          </Form.Select>
          <Form.Control.Feedback type="invalid">
            {t(f.errors.executor)}
          </Form.Control.Feedback>
        </Form.Group>

        <Form.Group className="mb-3" controlId="labels">
          <Form.Label>{t('labels')}</Form.Label>
          <Form.Select
            multiple
            value={f.values.labels}
            disabled={f.isSubmitting}
            onChange={f.handleChange}
            onBlur={f.handleBlur}
            isInvalid={f.errors.labels && f.touched.labels}
            name="labels"
          >
            {labels.map((label) => <option key={label.id} value={label.id}>{label.name}</option>)}
          </Form.Select>
          <Form.Control.Feedback type="invalid">
            {t(f.errors.labels)}
          </Form.Control.Feedback>
        </Form.Group>

        <Button variant="primary" type="submit">
          {t('edit')}
        </Button>
      </Form>
    </>
  );
};

export default EditTask;
