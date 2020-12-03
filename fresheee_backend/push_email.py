import pandas as pd
import smtplib
import imaplib
import email
import webbrowser
import os
import datetime
import unicodedata

from pprint import pprint
from email.message import EmailMessage
from email.header import decode_header
from firebase import firebase

from tqdm.notebook import tqdm

from time import sleep


def push_email(imap,firebase, email_index):
    res, data = imap.fetch(str(email_index), "(RFC822)")
    data_tuple, data_bytes = tuple(data)
    msg = email.message_from_bytes(data_tuple[1])
    subject = decode_header(msg["Subject"])[0][0].replace("\n", "").replace("\r", "").replace("Fwd: ", "")
    if isinstance(subject, bytes): 
        subject = subject.decode()
    sender = msg.get("From")
    body = ""

    if msg.is_multipart():
        for part in msg.walk():
            ctype = part.get_content_type()
            cdispo = str(part.get('Content-Disposition'))


            if ctype == 'text/html' and 'attachment' not in cdispo:
                body = part.get_payload(decode=True)  # decode
                break
            # if ctype == 'image/jpeg':
            #     jpeg = part.get_payload(decode=False)
            # if ctype == 'image/png':
            #     png = part.get_payload(decode=True)
            
    # not multipart - i.e. plain text, no attachments, keeping fingers crossed
    else:
        body = b.get_payload(decode=True)

    dt = datetime.datetime.strptime(msg["date"], "%a, %d %b %Y %H:%M:%S %z")
    unixtime = dt.timestamp()

    splits = sender.split()
    sender_name = splits[0]
    sender_email = splits[1][1:-1]

    out_email_dict = {}

    out_email_dict["subject"] = subject
    out_email_dict["sender"] = sender
    out_email_dict["sender_name"] = sender_name
    out_email_dict["sender_email"] = sender_email
    out_email_dict["datetime"] = unixtime
    out_email_dict["body_html"] = unicodedata.normalize("NFKD",  body.decode("cp1251")).replace('\n', ' ').replace('\r', ' ')

    data = out_email_dict
    result = firebase.post('/email_updates', data)



if __name__ == "__main__":
    # Email login
    EMAIL_ADDRESS = "fresheeeupdates@gmail.com"
    EMAIL_PASSWORD = "Desmondkz5683,."
    # create IMAP4 class the SSL
    imap = imaplib.IMAP4_SSL("imap.gmail.com")
    # authenticate
    imap.login(EMAIL_ADDRESS, EMAIL_PASSWORD)

    # Firebase
    fb = firebase.FirebaseApplication("https://fresheee-2020.firebaseio.com/", None)
    num_emails = len(fb.get('/email_updates', None))

    status, messages = imap.select("INBOX")

    print(f"{num_emails} emails in Firebase")
    print(f"{int(messages[0])} emails in Inbox")

    if num_emails < int(messages[0]):
        print("You've got mail!")
        for i in range(num_emails+1,  int(messages[0])+1):
            latest_email_index = num_emails
            push_email(imap=imap, firebase=fb, email_index=i)
    else:
        print("No new email!")


