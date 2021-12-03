package de.ingogriebsch.utils.testsmtpserver;

@FunctionalInterface
interface AuthCommandBehavior extends Behavior {

	Reply apply(String input);
}
