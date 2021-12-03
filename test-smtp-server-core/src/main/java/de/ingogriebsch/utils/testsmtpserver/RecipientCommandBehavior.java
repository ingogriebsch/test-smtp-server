package de.ingogriebsch.utils.testsmtpserver;

@FunctionalInterface
interface RecipientCommandBehavior extends Behavior {

	Reply apply(String from, String recipient);
}
