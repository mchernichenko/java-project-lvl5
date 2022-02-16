// @ts-check

import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Form, Button } from 'react-bootstrap';
import { useFormik } from 'formik';
import * as yup from 'yup';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

import routes from '../../routes.js';
import { useAuth, useNotify } from '../../hooks/index.js';

import getLogger from '../../lib/logger.js';

const log = getLogger('client');
log.enabled = true;

const getValidationSchema = () => yup.object().shape({});

const NewTask = () => {
  const { t } = useTranslation();
  // const dispatch = useDispatch();

  const navigate = useNavigate();
  const [data, setData] = useState({ executors: [], labels: [], statuses: [] });
  const {
    executors,
    labels,
    statuses,
  } = data;

  const auth = useAuth();
  const notify = useNotify();

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
        const promises = [
          axios.get(routes.apiUsers(), { headers: auth.getAuthHeader() }),
          axios.get(routes.apiLabels(), { headers: auth.getAuthHeader() }).catch(errorHandler),
          axios.get(routes.apiStatuses(), { headers: auth.getAuthHeader() }),
        ];
        const [
          { data: executorsData },
          labelsData,
          { data: statusesData },
        ] = await Promise.all(promises);

        setData({
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

  const f = useFormik({
    initialValues: {
      name: '',
      description: '',
      taskStatusId: null,
      executorId: null,
      labelIds: [],
    },
    validationSchema: getValidationSchema(),
    onSubmit: async (taskData, { setSubmitting, setErrors }) => {
      try {
        const requestTask = {
          name: taskData.name,
          description: taskData.description,
          executorId: parseInt(taskData.executorId, 10),
          taskStatusId: parseInt(taskData.taskStatusId, 10),
          labelIds: taskData.labelIds.map((id) => parseInt(id, 10)),
        };
        const response = await axios
          .post(routes.apiTasks(), requestTask, { headers: auth.getAuthHeader() });
        log('task.create', response);
        const from = { pathname: routes.tasksPagePath() };
        navigate(from);
        notify.addMessage(t('taskCreated'));
      } catch (e) {
        log('task.create.error', e);
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

        <Form.Group className="mb-3" controlId="taskStatusId">
          <Form.Label>{t('status')}</Form.Label>
          <Form.Select
            nullable
            value={f.values.taskStatusId}
            disabled={f.isSubmitting}
            onChange={f.handleChange}
            onBlur={f.handleBlur}
            isInvalid={f.errors.taskStatusId && f.touched.taskStatusId}
            name="taskStatusId"
          >
            <option value="">{null}</option>
            {statuses
              .map((status) => <option key={status.id} value={status.id}>{status.name}</option>)}
          </Form.Select>
          <Form.Control.Feedback type="invalid">
            {t(f.errors.taskStatusId)}
          </Form.Control.Feedback>
        </Form.Group>

        <Form.Group className="mb-3" controlId="executorId">
          <Form.Label>{t('executor')}</Form.Label>
          <Form.Select
            value={f.values.executorId}
            disabled={f.isSubmitting}
            onChange={f.handleChange}
            onBlur={f.handleBlur}
            isInvalid={f.errors.executorId && f.touched.executorId}
            name="executorId"
          >
            <option value="">{null}</option>
            {executors.map((executor) => <option key={executor.id} value={executor.id}>{`${executor.firstName} ${executor.lastName}`}</option>)}
          </Form.Select>
          <Form.Control.Feedback type="invalid">
            {t(f.errors.executorId)}
          </Form.Control.Feedback>
        </Form.Group>

        <Form.Group className="mb-3" controlId="labelIds">
          <Form.Label>{t('labels')}</Form.Label>
          <Form.Select
            multiple
            value={f.values.labelIds}
            disabled={f.isSubmitting}
            onChange={f.handleChange}
            onBlur={f.handleBlur}
            isInvalid={f.errors.labelIds && f.touched.labelIds}
            name="labelIds"
          >
            {labels.map((label) => <option key={label.id} value={label.id}>{label.name}</option>)}
          </Form.Select>
          <Form.Control.Feedback type="invalid">
            {t(f.errors.labelIds)}
          </Form.Control.Feedback>
        </Form.Group>

        <Button variant="primary" type="submit">
          {t('create')}
        </Button>
      </Form>
    </>
  );
};

export default NewTask;
