// @ts-check

import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Table, Form, Button } from 'react-bootstrap';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';

import { useAuth, useNotify } from '../../hooks/index.js';
import routes from '../../routes.js';

const Statuses = () => {
  const { t } = useTranslation();
  const [statuses, setStatuses] = useState([]);
  const auth = useAuth();
  const notify = useNotify();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const { data } = await axios.get(routes.apiStatuses(), { headers: auth.getAuthHeader() });
        setStatuses(data);
      } catch (e) {
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
    };
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const removeStatus = async (event, id) => {
    event.preventDefault();
    try {
      await axios.delete(`${routes.apiStatuses()}/${id}`, { headers: auth.getAuthHeader() });
      setStatuses(statuses.filter((status) => status.id !== id));
      notify.addMessage(t('statusRemoved'));
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

  return (
    <>
      <Link to={`${routes.statusesPagePath()}/new`}>{t('createStatus')}</Link>
      <Table striped hover>
        <thead>
          <tr>
            <th>{t('id')}</th>
            <th>{t('statusName')}</th>
            <th>{t('createDate')}</th>
          </tr>
        </thead>
        <tbody>
          {statuses.map((status) => (
            <tr key={status.id}>
              <td>{status.id}</td>
              <td>{status.name}</td>
              <td>{new Date(status.createdAt).toLocaleString('ru')}</td>
              <td>
                <Link to={`${routes.statusesPagePath()}/${status.id}/edit`}>{t('edit')}</Link>
                <Form onSubmit={(event) => removeStatus(event, status.id)}>
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

export default Statuses;
