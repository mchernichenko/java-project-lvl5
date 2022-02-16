// @ts-check

import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Table, Form, Button } from 'react-bootstrap';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';

import { useAuth, useNotify } from '../../hooks/index.js';
import routes from '../../routes.js';

const Labels = () => {
  const { t } = useTranslation();
  const [labels, setLabels] = useState([]);
  const auth = useAuth();
  const notify = useNotify();
  const navigate = useNavigate();
  useEffect(() => {
    const fetchData = async () => {
      try {
        const { data } = await axios.get(routes.apiLabels(), { headers: auth.getAuthHeader() });
        setLabels(data);
      } catch (e) {
        if (e.response?.status === 401) {
          const from = { pathname: routes.loginPagePath() };
          navigate(from);
          notify.addErrors([{ defaultMessage: t('Доступ запрещён! Пожалуйста, авторизируйтесь.') }]);
        } else {
          notify.addErrors([{ defaultMessage: e.message }]);
        }
      }
    };
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const removeLabel = async (event, id) => {
    event.preventDefault();
    try {
      await axios.delete(`${routes.apiLabels()}/${id}`, { headers: auth.getAuthHeader() });
      setLabels(labels.filter((label) => label.id !== id));
      notify.addMessage(t('labelRemoved'));
    } catch (e) {
      if (e.response?.status === 401) {
        const from = { pathname: routes.loginPagePath() };
        navigate(from);
        notify.addErrors([{ defaultMessage: t('Доступ запрещён! Пожалуйста, авторизируйтесь.') }]);
      } else {
        notify.addErrors([{ defaultMessage: e.message }]);
      }
    }
  };

  return (
    <>
      <Link to={`${routes.labelsPagePath()}/new`}>{t('createLabel')}</Link>
      <Table striped hover>
        <thead>
          <tr>
            <th>{t('id')}</th>
            <th>{t('statusName')}</th>
            <th>{t('createDate')}</th>
          </tr>
        </thead>
        <tbody>
          {labels.map((label) => (
            <tr key={label.id}>
              <td>{label.id}</td>
              <td>{label.name}</td>
              <td>{new Date(label.createdAt).toLocaleString('ru')}</td>
              <td>
                <Link to={`${routes.labelsPagePath()}/${label.id}/edit`}>{t('edit', { defaultValue: 'Изменить' })}</Link>
                <Form onSubmit={(event) => removeLabel(event, label.id)}>
                  <Button type="submit" variant="link">Удалить</Button>
                </Form>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </>
  );
};

export default Labels;
