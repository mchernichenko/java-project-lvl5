// @ts-check

import React from 'react';
import { useTranslation } from 'react-i18next';
import { Form, Button } from 'react-bootstrap';
import { useFormik } from 'formik';
import * as yup from 'yup';
import axios from 'axios';
import { useLocation, useNavigate } from 'react-router-dom';

import { useAuth, useNotify } from '../hooks/index.js';
import routes from '../routes.js';

const getValidationSchema = () => yup.object().shape({});

const Login = () => {
  const { t } = useTranslation();
  const location = useLocation();
  const navigate = useNavigate();

  const auth = useAuth();
  const notify = useNotify();

  const f = useFormik({
    initialValues: {
      email: '',
      password: '',
    },
    validationSchema: getValidationSchema(),
    onSubmit: async (formData, { setSubmitting, setErrors }) => {
      try {
        const userData = { email: formData.email, password: formData.password };
        const { data: token } = await axios.post(routes.apiLogin(), userData);

        auth.logIn({ ...formData, token });
        const { from } = location.state || { from: { pathname: routes.homePagePath() } };
        navigate(from);
        notify.addMessage(t('loginSuccess'));
      } catch (e) {
        if (e.response?.status === 401) {
          notify.addErrors([{ defaultMessage: t('loginFail') }]);
        } else if (e.response?.status === 422 && Array.isArray(e.response?.data)) {
          const errors = e.response?.data
            .reduce((acc, err) => ({ ...acc, [err.field]: err.defaultMessage }), {});
          setErrors(errors);
        } else {
          notify.addErrors([{ defaultMessage: e.message }]);
        }
        setSubmitting(false);
      }
    },
    validateOnBlur: false,
    validateOnChange: false,
  });
  return (
    <>
      <h1 className="my-4">{t('login')}</h1>
      <Form onSubmit={f.handleSubmit}>
        <Form.Group className="mb-3" controlId="formBasicEmail">
          <Form.Label>{t('email')}</Form.Label>
          <Form.Control
            type="text"
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

        <Form.Group className="mb-3" controlId="formBasicPassword">
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

export default Login;
