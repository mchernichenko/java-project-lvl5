// @ts-check

import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useParams, useNavigate, Link } from 'react-router-dom';
import {
  Card, Button, Container, Row, Col, Form,
} from 'react-bootstrap';
import axios from 'axios';

import routes from '../../routes.js';
import { useAuth, useNotify } from '../../hooks/index.js';

import getLogger from '../../lib/logger.js';

const log = getLogger('client');
log.enabled = true;

const Task = () => {
  const { t } = useTranslation();
  const params = useParams();
  const auth = useAuth();
  const notify = useNotify();
  const navigate = useNavigate();

  const [task, setTask] = useState({});

  useEffect(() => {
    const fetchData = async () => {
      try {
        const { data: taskData } = await axios.get(`${routes.apiTasks()}/${params.taskId}`, { headers: auth.getAuthHeader() });
        setTask(taskData);
      } catch (e) {
        if (e.response?.status === 401) {
          const from = { pathname: routes.loginPagePath() };
          navigate(from);
          notify.addErrors([{ defaultMessage: t('Доступ запрещён! Пожалуйста, авторизируйтесь.') }]);
        } else if (e.response?.status === 422 && Array.isArray(e.response?.data)) {
          notify.addErrors(e.response?.data);
        } else {
          notify.addErrors([{ defaultMessage: e.message }]);
        }
      }
    };
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const removeTask = async (event, id) => {
    event.preventDefault();
    try {
      await axios.delete(`${routes.apiTasks()}/${id}`, { headers: auth.getAuthHeader() });
      const from = { pathname: routes.tasksPagePath() };
      navigate(from);
      notify.addMessage(t('taskRemoved'));
    } catch (e) {
      if (e.response?.status === 401) {
        const from = { pathname: routes.loginPagePath() };
        navigate(from);
        notify.addErrors([{ defaultMessage: t('Доступ запрещён! Пожалуйста, авторизируйтесь.') }]);
      } else if (e.response?.status === 403) {
        notify.addErrors([{ defaultMessage: t('Задачу может удалить только её автор') }]);
      } else if (e.response?.status === 422 && Array.isArray(e.response?.data)) {
        notify.addErrors(e.response?.data);
      } else {
        notify.addErrors([{ defaultMessage: e.message }]);
      }
    }
  };

  return (
    <Card>
      <Card.Header className="bg-secondary text-white">
        <Card.Title>{task.name}</Card.Title>
      </Card.Header>
      <Card.Body>
        <p>{task.description}</p>
        <Container>
          <Row>
            <Col>
              {t('author')}
            </Col>
            <Col>
              {`${task.author?.firstName ?? ''} ${task.author?.lastName ?? ''}`}
            </Col>
          </Row>
          <Row>
            <Col>
              {t('executor')}
            </Col>
            <Col>
              {`${task.executor?.firstName ?? ''} ${task.executor?.lastName ?? ''}`}
            </Col>
          </Row>
          <Row>
            <Col>
              {t('status')}
            </Col>
            <Col>
              {task.taskStatus?.name}
            </Col>
          </Row>
          <Row>
            <Col>
              {t('createDate')}
            </Col>
            <Col>
              {new Date(task.createdAt).toLocaleString('ru')}
            </Col>
          </Row>
          <Row>
            <Col>
              {t('labels')}
              :
              <ul>
                {task?.labels?.map((label) => (<li key={label.id}>{label.name}</li>))}
              </ul>
            </Col>
          </Row>
          <Row>
            <Col>
              <Link to={`${routes.tasksPagePath()}/${task.id}/edit`}>{t('edit')}</Link>
              <Form onSubmit={(e) => removeTask(e, task.id)}>
                <Button type="submit" variant="link">Удалить</Button>
              </Form>
            </Col>
          </Row>
        </Container>
      </Card.Body>
    </Card>
  );
};

export default Task;
