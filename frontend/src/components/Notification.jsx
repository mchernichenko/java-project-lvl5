// @ts-check

import React from 'react';
import { Alert } from 'react-bootstrap';
import { useTranslation } from 'react-i18next';
import { useNotify } from '../hooks/index.js';

const Notification = () => {
  const { messages } = useNotify();
  const { t } = useTranslation();

  return (
    <>
      {messages.map((message) => (
        <Alert key={message.id} show variant={message.type}>
          {message.field ? `Поле "${t(message.field)}" - ${message.defaultMessage}` : message.defaultMessage}
        </Alert>
      ))}
    </>
  );
};

export default Notification;
