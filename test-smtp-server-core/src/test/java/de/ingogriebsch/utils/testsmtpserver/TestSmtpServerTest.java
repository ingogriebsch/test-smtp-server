package de.ingogriebsch.utils.testsmtpserver;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TestSmtpServerTest {

	@Test
	void should() {
		TestSmtpServer smtpServer = TestSmtpServer.builder().randomPort().build();
		smtpServer.start();
		smtpServer.reset();
		smtpServer.stop();
	}

	@Nested
	class TestSmtpServerBuilderTest {

		@Test
		void should() {
		}
	}

	@Nested
	class RandomPortSupplierTest {

		@Test
		void should() {
		}
	}
}
