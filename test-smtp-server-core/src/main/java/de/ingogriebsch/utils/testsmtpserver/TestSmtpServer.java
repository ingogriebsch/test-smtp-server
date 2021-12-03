package de.ingogriebsch.utils.testsmtpserver;

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.server.SMTPServer;

import lombok.NonNull;

public final class TestSmtpServer {
	private final List<Behavior> behaviors;
	private final SMTPServer smtpServer;
	private boolean started = false;
	private boolean stopped = false;

	private TestSmtpServer(SMTPServer smtpServer, List<Behavior> behaviors) {
		this.smtpServer = smtpServer;
		this.behaviors = behaviors;
	}

	public static TestSmtpServerBuilder builder() {
		return builder(new RandomPortSupplier());
	}

	public static TestSmtpServerBuilder builder(@NonNull Supplier<Integer> randomPortSupplier) {
		return new TestSmtpServerBuilder(randomPortSupplier);
	}

	public void start() {
		if (started) {
			throw new IllegalStateException("The Test SMTP Server can only be started once!");
		}

		if (stopped) {
			throw new IllegalStateException("The Test SMTP Server can not be started once it was stopped!");
		}

		smtpServer.start();
		started = true;
	}

	public void stop() {
		if (!started || stopped) {
			return;
		}

		smtpServer.stop();
		started = false;
		stopped = true;
	}

	public void reset() {
		behaviors.clear();
	}

	public TestSmtpServer withBehavior(@NonNull Behavior behavior) {
		behaviors.add(behavior);
		return this;
	}

	public static final class TestSmtpServerBuilder {

		private final BehaviorBasedMessageHandlerFactory messageHandlerFactory = new BehaviorBasedMessageHandlerFactory();
		private final Supplier<Integer> randomPortSupplier;

		private Integer port;

		private TestSmtpServerBuilder(Supplier<Integer> randomPortSupplier) {
			this.randomPortSupplier = randomPortSupplier;
		}

		public TestSmtpServerBuilder randomPort() {
			return port(randomPortSupplier.get());
		}

		public TestSmtpServerBuilder port(@NonNull Integer port) {
			assertValidPort(port);
			this.port = port;
			return this;
		}

		public TestSmtpServer build() {
			assertValidPort(port);

			SMTPServer smtpServer = SMTPServer //
					.port(port) //
					.messageHandlerFactory(messageHandlerFactory) //
					.build();
			smtpServer.getAuthenticationHandlerFactory();
			List<Behavior> behaviors = messageHandlerFactory.getBehaviors();

			return new TestSmtpServer(smtpServer, behaviors);
		}

		private static void assertValidPort(Integer port) {
			if (port < 1 || port > 65535) {
				throw new IllegalArgumentException(
						format("The given port must be between 1 and 65535 but was %d!", port));
			}
		}
	}

	private static class BehaviorBasedMessageHandlerFactory implements MessageHandlerFactory {
		private final List<Behavior> behaviors = new ArrayList<>();

		List<Behavior> getBehaviors() {
			return behaviors;
		}

		@Override
		public MessageHandler create(MessageContext ctx) {
			return new BehaviorBasedMessageHandler(ctx, behaviors);
		}

		private static class BehaviorBasedMessageHandler implements MessageHandler {

			private final List<Behavior> behaviors;

			private String from;
			private String recipient;

			public BehaviorBasedMessageHandler(MessageContext context, List<Behavior> behaviors) {
				this.behaviors = behaviors;
			}

			@Override
			public void from(String from) throws RejectException {
				this.from = from;

				Optional<Reply> reply = behaviors.stream() //
						.filter(b -> MailCommandBehavior.class.isAssignableFrom(b.getClass())) //
						.map(MailCommandBehavior.class::cast) //
						.map(b -> b.apply(from)) //
						.filter(Objects::nonNull) //
						.limit(1) //
						.findAny();

				if (reply.isPresent()) {
					throw new RejectException(reply.get().getCode().getValue(), reply.get().getMessage());
				}
			}

			@Override
			public void recipient(String recipient) throws RejectException {
				this.recipient = recipient;

				Optional<Reply> reply = behaviors.stream() //
						.filter(b -> RecipientCommandBehavior.class.isAssignableFrom(b.getClass())) //
						.map(RecipientCommandBehavior.class::cast) //
						.map(b -> b.apply(from, recipient)) //
						.filter(Objects::nonNull) //
						.limit(1) //
						.findAny();

				if (reply.isPresent()) {
					throw new RejectException(reply.get().getCode().getValue(), reply.get().getMessage());
				}
			}

			@Override
			public String data(InputStream data) throws RejectException, TooMuchDataException, IOException {
				Optional<Reply> reply = behaviors.stream() //
						.filter(b -> DataCommandBehavior.class.isAssignableFrom(b.getClass())) //
						.map(DataCommandBehavior.class::cast) //
						.map(b -> b.apply(from, recipient, data)) //
						.filter(Objects::nonNull) //
						.limit(1) //
						.findAny();

				if (reply.isPresent()) {
					throw new RejectException(reply.get().getCode().getValue(), reply.get().getMessage());
				}

				return null;
			}

			@Override
			public void done() {
				// nothing to do here...
			}
		}
	}
}
