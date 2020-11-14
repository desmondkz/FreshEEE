package com.ntu.fresheee;

public class EmailUpdate implements Comparable<EmailUpdate> {

    public String body_html, sender, sender_email, sender_name, subject;
    public int datetime;

    public EmailUpdate() {

    }

    public EmailUpdate(String body_html, String sender, String sender_email, String sender_name, String subject, int datetime) {

        this.body_html = body_html;
        this.sender = sender;
        this.sender_email = sender_email;
        this.sender_name = sender_name;
        this.subject = subject;
        this.datetime = datetime;

    }

    public int getEmailDate() {
        return datetime;
    }

    @Override
    public int compareTo(EmailUpdate emailUpdate) {
        int comparedate = ((EmailUpdate) emailUpdate).getEmailDate();
        return comparedate - this.datetime;
    }
}
