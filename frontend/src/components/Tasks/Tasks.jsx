// @ts-check

import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useTranslation } from 'react-i18next';
import { Table, Form, Button } from 'react-bootstrap';
import axios from 'axios';
import { Link, useHistory } from 'react-router-dom';

import handleError from '../../utils.js';
import { useAuth, useNotify } from '../../hooks/index.js';
import routes from '../../routes.js';
import TaskFilter from './TaskFilter.jsx';
import { actions as taskActions } from '../../slices/tasksSlice.js';

const Tasks = () => {
  const { t } = useTranslation();
  const tasks = useSelector((state) => state.tasks?.tasks);
  const [filteredTasks, setFilteredTasks] = useState(null);
  const auth = useAuth();
  const notify = useNotify();
  const history = useHistory();
  const dispatch = useDispatch();

  if (!tasks) {
    return null;
  }
  const removeTask = async (event, id) => {
    event.preventDefault();
    try {
      await axios.delete(`${routes.apiTasks()}/${id}`, { headers: auth.getAuthHeader() });
      dispatch(taskActions.removeTask((id)));
      notify.addMessage('taskRemoved');
    } catch (e) {
      handleError(e, notify, history, auth);
      if (e.response?.status === 403) {
        notify.addErrors([{ defaultMessage: t('Задачу может удалить только её автор') }]);
      }
    }
  };

  return (
    <>
      <Link to={`${routes.tasksPagePath()}/new`}>{t('createTask')}</Link>
      <TaskFilter foundTasks={setFilteredTasks} />
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
          {(filteredTasks ?? tasks).map((task) => (
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
