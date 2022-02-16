// @ts-check

import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useFormik } from 'formik';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import {
  Card, Button, Col, Form, Row,
} from 'react-bootstrap';

import { useAuth, useNotify } from '../../hooks/index.js';
import routes from '../../routes.js';

const TaskFilter = (props) => {
  const { foundTasks: handler } = props;
  const auth = useAuth();
  const navigate = useNavigate();
  const { t } = useTranslation();
  const notify = useNotify();
  const [data, setData] = useState({ executors: [], labels: [], statuses: [] });
  const {
    executors,
    labels,
    statuses,
  } = data;

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
          executors: executorsData ?? [],
          labels: labelsData ? labelsData.data : [],
          statuses: statusesData ?? [],
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
      taskStatusId: null,
      executorId: null,
      labelId: null,
      isMyTasks: false,
    },
    onSubmit: async (formData, { setSubmitting }) => {
      try {
        const params = {};
        if (formData.isMyTasks) {
          const author = executors.find((user) => user.email === auth?.user?.email);
          params.authorId = author.id;
        }

        if (formData.taskStatusId) {
          params.taskStatus = formData.taskStatusId;
        }

        if (formData.executorId) {
          params.executorId = formData.executorId;
        }

        if (formData.labelId) {
          params.labels = formData.labelId;
        }

        const { data: response } = await axios
          .get(routes.apiTasks(), { params, headers: auth.getAuthHeader() });

        handler(response);
      } catch (e) {
        setSubmitting(false);
        if (e.response?.status === 401) {
          const from = { pathname: routes.loginPagePath() };
          navigate(from);
          notify.addErrors([{ defaultMessage: t('Доступ запрещён! Пожалуйста, авторизируйтесь.') }]);
        } else if (e.response?.status === 422) {
          notify.addErrors(e.response?.data);
        } else {
          notify.addErrors([{ defaultMessage: e.message }]);
        }
      }
    },
    validateOnBlur: false,
    validateOnChange: false,
  });

  return (
    <Card bg="light">
      <Card.Body>
        <Form onSubmit={f.handleSubmit}>
          <Row className="g-2">
            <Col md>
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
                  {statuses.map((status) => (
                    <option key={status.id} value={status.id}>
                      {status.name}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md>
              <Form.Group className="mb-3" controlId="executorId">
                <Form.Label>{t('executor')}</Form.Label>
                <Form.Select
                  nullable
                  value={f.values.executorId}
                  disabled={f.isSubmitting}
                  onChange={f.handleChange}
                  onBlur={f.handleBlur}
                  isInvalid={f.errors.executorId && f.touched.executorId}
                  name="executorId"
                >
                  <option value="">{null}</option>
                  {executors.map((executor) => (
                    <option key={executor.id} value={executor.id}>
                      {`${executor.firstName} ${executor.lastName}`}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md>
              <Form.Group className="mb-3" controlId="labelId">
                <Form.Label>{t('label')}</Form.Label>
                <Form.Select
                  nullable
                  value={f.values.labelId}
                  disabled={f.isSubmitting}
                  onChange={f.handleChange}
                  onBlur={f.handleBlur}
                  isInvalid={f.errors.labelId && f.touched.labelId}
                  name="labelId"
                >
                  <option value="">{null}</option>
                  {labels.map((label) => (
                    <option key={label.id} value={label.id}>{label.name}</option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
          </Row>
          <Form.Group className="mb-3" controlId="isMyTasks">
            <Form.Check
              type="checkbox"
              label={t('isMyTasks')}
              onChange={f.handleChange}
              value={f.values.isMyTasks}
            />
          </Form.Group>

          <Button variant="primary" type="submit">
            {t('show')}
          </Button>
        </Form>
      </Card.Body>
    </Card>
  );
};

export default TaskFilter;
