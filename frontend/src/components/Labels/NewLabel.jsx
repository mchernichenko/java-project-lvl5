// @ts-check

import React from 'react';
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

const getValidationSchema = () => yup.object().shape({});

const NewLabel = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const auth = useAuth();
  const notify = useNotify();

  const f = useFormik({
    initialValues: {
      name: '',
    },
    validationSchema: getValidationSchema(),
    onSubmit: async ({ name }, { setSubmitting, setErrors }) => {
      const label = { name };
      try {
        // const data = await api.createLabel(label);
        log('label.create', label);

        await axios.post(routes.apiLabels(), label, { headers: auth.getAuthHeader() });
        const from = { pathname: routes.labelsPagePath() };
        navigate(from);
        notify.addMessage(t('labelCreated'));
        // dispatch(actions.addLabel(label));
      } catch (e) {
        log('label.create.error', e);
        setSubmitting(false);
        if (e.response?.status === 401) {
          const from = { pathname: routes.loginPagePath() };
          navigate(from);
          notify.addErrors([{ defaultMessage: t('Доступ запрещён! Пожалуйста, авторизируйтесь.') }]);
        } else if (e.response?.status === 422 && e.response?.data) {
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
      <h1 className="my-4">{t('labelCreating')}</h1>
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
          {t('create')}
        </Button>
      </Form>
    </>
  );
};

export default NewLabel;
