// @ts-check

import React from 'react';
import { useTranslation } from 'react-i18next';
import { Form, Button } from 'react-bootstrap';
import { useFormik } from 'formik';
import axios from 'axios';
import * as yup from 'yup';
import { useNavigate } from 'react-router-dom';

import { useNotify } from '../hooks/index.js';
import routes from '../routes.js';

import getLogger from '../lib/logger.js';

const log = getLogger('registration');
log.enabled = true;

const getValidationSchema = () => yup.object().shape({});

const Registration = () => {
  const { t } = useTranslation();
  const notify = useNotify();
  const navigate = useNavigate();

  const f = useFormik({
    initialValues: {
      firstName: '',
      lastName: '',
      email: '',
      password: '',
    },
    validationSchema: getValidationSchema(),
    onSubmit: async (userData, { setSubmitting, setErrors }) => {
      try {
        const user = {
          ...userData,
        };
        await axios.post(routes.apiUsers(), user);

        const from = { pathname: routes.loginPagePath() };
        notify.addMessage(t('registrationSuccess'));
        navigate(from);
        // dispatch(actions.addTask(task));
      } catch (e) {
        log('create.error', e);
        setSubmitting(false);
        // TODO: убрать обработку ошибок в ErrorBoundaries
        if (e.response?.status === 400) {
          notify.addErrors([{ defaultMessage: t('registrationFail') }]);
        } else if (e.response?.status === 422 && Array.isArray(e.response?.data)) {
          const errors = e.response?.data
            .reduce((acc, err) => ({ ...acc, [err.field]: err.defaultMessage }), {});
          log('validation errors', errors);
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
      <h1 className="my-4">{t('signup')}</h1>
      <Form onSubmit={f.handleSubmit}>
        <Form.Group className="mb-3" controlId="firstName">
          <Form.Label>{t('name')}</Form.Label>
          <Form.Control
            type="text"
            value={f.values.firstName}
            disabled={f.isSubmitting}
            onChange={f.handleChange}
            onBlur={f.handleBlur}
            isInvalid={f.errors.firstName && f.touched.firstName}
            name="firstName"
          />
          <Form.Control.Feedback type="invalid">
            {t(f.errors.firstName)}
          </Form.Control.Feedback>
        </Form.Group>

        <Form.Group className="mb-3" controlId="lastName">
          <Form.Label>{t('surname')}</Form.Label>
          <Form.Control
            type="text"
            value={f.values.lastName}
            disabled={f.isSubmitting}
            onChange={f.handleChange}
            onBlur={f.handleBlur}
            isInvalid={f.errors.lastName && f.touched.lastName}
            name="lastName"
          />
          <Form.Control.Feedback type="invalid">
            {t(f.errors.lastName)}
          </Form.Control.Feedback>
        </Form.Group>

        <Form.Group className="mb-3" controlId="email">
          <Form.Label>{t('email')}</Form.Label>
          <Form.Control
            type="email"
            value={f.values.email}
            disabled={f.isSubmitting}
            onChange={f.handleChange}
            onBlur={f.handleBlur}
            isInvalid={f.errors.email && f.touched.email}
            name="email"
          />
          <Form.Control.Feedback type="invalid">
            {t(f.errors.email)}
          </Form.Control.Feedback>
        </Form.Group>

        <Form.Group className="mb-3" controlId="password">
          <Form.Label>{t('password')}</Form.Label>
          <Form.Control
            type="password"
            value={f.values.password}
            disabled={f.isSubmitting}
            onChange={f.handleChange}
            onBlur={f.handleBlur}
            isInvalid={f.errors.password && f.touched.password}
            name="password"
          />
          <Form.Control.Feedback type="invalid">
            {t(f.errors.password)}
          </Form.Control.Feedback>
        </Form.Group>

        <Button variant="primary" type="submit">
          Submit
        </Button>
      </Form>
    </>
  );
};

export default Registration;
