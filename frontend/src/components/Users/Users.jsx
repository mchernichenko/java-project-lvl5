// @ts-check

import React from 'react';
import { useSelector } from 'react-redux';
import { useTranslation } from 'react-i18next';
import { Table, Form, Button } from 'react-bootstrap';
import axios from 'axios';
import { Link, useHistory } from 'react-router-dom';

import { useAuth, useNotify } from '../../hooks/index.js';
import routes from '../../routes.js';
import handleError from '../../utils.js';

import getLogger from '../../lib/logger.js';

const log = getLogger('user');
log.enabled = true;

const UsersComponent = () => {
  const { t } = useTranslation();
  const auth = useAuth();
  const notify = useNotify();
  const history = useHistory();

  const { users } = useSelector((state) => state.users);

  const removeUserHandler = async (event, id) => {
    log(event, id);
    event.preventDefault();
    try {
      await axios.delete(`${routes.apiUsers()}/${id}`, { headers: auth.getAuthHeader() });
      auth.logOut();
      log('success');
      notify.addMessage('userDeleted');
    } catch (e) {
      if (e.response?.status === 403) {
        notify.addErrors([{ text: 'userDeleteDenied' }]);
      } else {
        handleError(e, notify, history, auth);
      }
    }
  };

  if (!users) {
    return null;
  }

  return (
    <Table striped hover>
      <thead>
        <tr>
          <th>{t('id')}</th>
          <th>{t('fullName')}</th>
          <th>{t('email')}</th>
          <th>{t('createDate')}</th>
          <th>{null}</th>
        </tr>
      </thead>
      <tbody>
        {users.map((user) => (
          <tr key={user.id}>
            <td>{user.id}</td>
            <td>{`${user.firstName} ${user.lastName}`}</td>
            <td>{user.email}</td>
            <td>{new Date(user.createdAt).toLocaleString('ru')}</td>
            <td>
              <Link to={`${routes.usersPagePath()}/${user.id}/edit`}>{t('edit')}</Link>
              <Form onSubmit={(event) => removeUserHandler(event, user.id)}>
                <Button type="submit" variant="link">Удалить</Button>
              </Form>
            </td>
          </tr>
        ))}
      </tbody>
    </Table>
  );
};

export default UsersComponent;
