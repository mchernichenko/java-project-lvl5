// @ts-check

import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { Form, Button } from 'react-bootstrap';
import { useFormik } from 'formik';
import * as yup from 'yup';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';

import routes from '../../routes.js';
import { useAuth, useNotify } from '../../hooks/index.js';

import getLogger from '../../lib/logger.js';

const log = getLogger('client');

const getValidationSchema = () => yup.object().shape({});

const EditStatus = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [status, setStatus] = useState({});
  const params = useParams();
  const auth = useAuth();
  const notify = useNotify();
  // const dispatch = useDispatch();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const { data } = await axios.get(`${routes.apiStatuses()}/${params.statusId}`, { headers: auth.getAuthHeader() });
        setStatus(data);
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

  const f = useFormik({
    enableReinitialize: true,
    initialValues: {
      name: status.name,
    },
    validationSchema: getValidationSchema(),
    onSubmit: async ({ name }, { setSubmitting, setErrors }) => {
      const newStatus = { name };
      try {
        // TODO: api
        await axios.put(`${routes.apiStatuses()}/${params.statusId}`, newStatus, { headers: auth.getAuthHeader() });
        log('status.edit', newStatus);
        const from = { pathname: routes.statusesPagePath() };
        navigate(from);
        notify.addMessage(t('statusEdited'));
        // dispatch(actions.addStatus(label));
      } catch (e) {
        log('label.edit.error', e);
        setSubmitting(false);
        if (e.response?.status === 401) {
          const from = { pathname: routes.loginPagePath() };
          navigate(from);
          notify.addErrors([{ defaultMessage: t('Доступ запрещён! Пожалуйста, авторизируйтесь.') }]);
        } else if (e.response?.status === 422) {
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
      <h1 className="my-4">{t('statusEdit')}</h1>
      <Form onSubmit={f.handleSubmit}>
        <Form.Group className="mb-3">
          <Form.Label>{t('naming')}</Form.Label>
          <Form.Control
            className="mb-2"
            disabled={f.isSubmitting}
            onChange={f.handleChange}
            onBlur={f.handleBlur}
            value={f.values.name}
            isInvalid={f.errors.name && f.touched.name}
            name="name"
            id="name"
            type="text"
          />
          <Form.Control.Feedback type="invalid">
            {t(f.errors.name)}
          </Form.Control.Feedback>
        </Form.Group>
        <Button variant="primary" type="submit" disabled={f.isSubmitting}>
          {t('edit')}
        </Button>
      </Form>
    </>
  );
};

export default EditStatus;
