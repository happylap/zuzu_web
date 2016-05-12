# encoding: utf-8

import sys
import time
import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

args = sys.argv
IS_LOCAL = 0
if len(args) > 1 and args[1] == "IS_LOCAL":
    IS_LOCAL = 1


if IS_LOCAL:
    from notify import constants
else:
    import constants

class MailSender:
    def __init__(self, user=constants.ZUZU_EMAIL_USER, password=constants.ZUZU_EMAIL_PASSWD,
                 host='smtp.gmail.com', port='587', encoding="utf-8"):
        self.user = user
        self.host = host
        self.port = port
        self.encoding = encoding
        self.login(password)
        self.num_mail_sent = 0

    def send(self, to=None, subject=None, contents=None, cc=None):
        """ Use this to send an email with gmail"""
        if subject is not None and constants.PRODUCT_MODE == False:
            subject += ' (Development)'
        addresses = self._resolve_addresses(to, cc)
        if not addresses['recipients']:
            return {}
        msg = self._prepare_message(addresses, subject, contents)
        self._attempt_send(addresses['recipients'], msg.as_string())

    def _attempt_send(self, recipients, msg_string):
        attempts = 0
        while attempts < 3:
            try:
                result = self.server.sendmail(self.user, recipients, msg_string)
                self.num_mail_sent += 1
                return result
            except smtplib.SMTPServerDisconnected as e:
                attempts += 1
                time.sleep(attempts * 3)
        return False

    def login(self, password):
        """
        Login to the SMTP server using password.
        """
        self.server = smtplib.SMTP(self.host, self.port)
        self.server.starttls()
        self.server.login(self.user, password)

    def _resolve_addresses(self, to, cc):
        """ Handle the targets addresses, adding aliases when defined """
        addresses = {'recipients': []}
        if to is not None:
            self._make_addr_alias_target(to, addresses, 'To')
        else:
            addresses['recipients'].append(self.user)

        if cc is not None:
            self._make_addr_alias_target(cc, addresses, 'Cc')

        return addresses

    def _prepare_message(self, addresses, subject, contents):
        """ Prepare a MIME message """

        msg = MIMEMultipart()
        self._add_subject(msg, subject)
        self._add_recipients_headers(msg, addresses)

        body = contents
        msg.attach(MIMEText(body, 'html', _charset=self.encoding))

        return msg

    def _add_recipients_headers(self, msg, addresses):
        msg['From'] = '{} <{}>'.format(self.user, self.user)
        if 'To' in addresses:
            msg['To'] = addresses['To']
        else:
            msg['To'] = self.user
        if 'Cc' in addresses:
            msg['Cc'] = addresses['Cc']

    @staticmethod
    def _make_addr_alias_target(x, addresses, which):
        if isinstance(x, str):
            addresses['recipients'].append(x)
            addresses[which] = x
        elif isinstance(x, list) or isinstance(x, tuple):
            if not all([isinstance(k, str) for k in x]):
                raise RuntimeError('email address error')
            addresses['recipients'].extend(x)
            addresses[which] = '; '.join(x)
        elif isinstance(x, dict):
            addresses['recipients'].extend(x.keys())
            addresses[which] = '; '.join(x.values())
        else:
            raise RuntimeError('email address error')

    @staticmethod
    def _add_subject(msg, subject):
        if not subject:
            return
        if isinstance(subject, list):
            subject = ' '.join(subject)
        msg['Subject'] = subject


if __name__ == '__main__':
    m_to = ['eechih@gmail.com']
    m_subject = '豬豬快租 Test11'
    m_body = '<h1>豬豬快租 Test</h1> test'
    m_cc = ['eechih@gmail.com']
    MailSender().send(m_to, m_subject, m_body, m_cc)
