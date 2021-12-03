package de.ingogriebsch.utils.testsmtpserver;

@FunctionalInterface
interface MailCommandBehavior extends Behavior {

	Reply apply(String from);

}
