package de.ingogriebsch.utils.testsmtpserver;

import java.io.InputStream;

@FunctionalInterface
interface DataCommandBehavior extends Behavior {

	Reply apply(String from, String recipient, InputStream data);
}
