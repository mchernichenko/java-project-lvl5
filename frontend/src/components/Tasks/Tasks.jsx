// @ts-check

import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { Table, Form, Button } from 'react-bootstrap';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';

import { useAuth, useNotify } from '../../hooks/index.js';
import routes from '../../routes.js';
import TaskFilter from './TaskFilter.jsx';

const Tasks = () => {
  const { t } = useTranslation();
  const [tasks, setTasks] = useState([]);
  const auth = useAuth();
  const notify = useNotify();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const { data } = await axios.get(routes.apiTasks(), { headers: auth.getAuthHeader() });
        setTasks(data);
      } catch (e) {
        if (e.response?.status === 401) {
          const from = { pathname: routes.loginPagePath() };
          navigate(from);
          notify.addErrors([{ defaultMessage: t('Доступ запрещён! Пожалуйста, авторизируйтесь.') }]);
        } else if (e.response?.status === 422 && e.response?.data) {
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
      setTasks(tasks.filter((task) => task.id !== id));
      notify.addMessage(t('taskRemoved'));
    } catch (e) {
      // notify.addErrors(e.response?.data);
      if (e.response?.status === 401) {
        const from = { pathname: routes.loginPagePath() };
        navigate(from);
        notify.addErrors([{ defaultMessage: t('Доступ запрещён! Пожалуйста, авторизируйтесь.') }]);
      } else if (e.response?.status === 403) {
        notify.addErrors([{ defaultMessage: t('Задачу может удалить только её автор') }]);
      } else if (e.response?.status === 422 && e.response?.data) {
        notify.addErrors(e.response?.data);
      } else {
        notify.addErrors([{ defaultMessage: e.message }]);
      }
    }
  };

  return (
    <>
      <Link to={`${routes.tasksPagePath()}/new`}>{t('createTask')}</Link>
      <TaskFilter foundTasks={(filteredTasks) => setTasks(filteredTasks)} />
      <Table striped hover>
        <thead>
          <tr>
            <th>{t('id')}</th>
            <th>{t('naming')}</th>
            <th>{t('status')}</th>
            <th>{t('author')}</th>
            <th>{t('executor')}</th>
            <th>{t('createDate')}</th>
            <th>{null}</th>
          </tr>
        </thead>
        <tbody>
          {tasks.map((task) => (
            <tr key={task.id}>
              <td>{task?.id}</td>
              <td>
                <Link to={`${routes.tasksPagePath()}/${task.id}`}>{task.name}</Link>
              </td>
              <td>{task.taskStatus?.name}</td>
              <td>{`${task.author?.firstName} ${task.author?.lastName}`}</td>
              <td>{`${task.executor?.firstName ?? ''} ${task.executor?.lastName ?? ''}`}</td>
              <td>{new Date(task.createdAt).toLocaleString('ru')}</td>
              <td>
                <Link to={`${routes.tasksPagePath()}/${task.id}/edit`}>{t('edit')}</Link>
                <Form onSubmit={(event) => removeTask(event, task.id)}>
                  <Button type="submit" variant="link">{t('remove')}</Button>
                </Form>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </>
  );
};

export default Tasks;
