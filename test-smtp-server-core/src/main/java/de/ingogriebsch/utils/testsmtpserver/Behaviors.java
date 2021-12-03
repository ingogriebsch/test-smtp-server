package de.ingogriebsch.utils.testsmtpserver;

import java.io.InputStream;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Behaviors {

	public static Behavior mailCommandReplies(@NonNull Reply reply) {
		return (MailCommandBehavior) from -> reply;
	}

	public static Behavior mailCommandRepliesIf(@NonNull Reply reply, @NonNull MailCommandPredicate predicate) {
		return (MailCommandBehavior) from -> predicate.test(from) ? reply : null;
	}

	public static Behavior mailCommandRepliesIfNot(@NonNull Reply reply, @NonNull MailCommandPredicate predicate) {
		return (MailCommandBehavior) from -> predicate.test(from) ? null : reply;
	}

	public static Behavior recipientCommandReplies(@NonNull Reply reply) {
		return (RecipientCommandBehavior) (from, to) -> reply;
	}

	public static Behavior recipientCommandRepliesIf(@NonNull Reply reply,
			@NonNull RecipientCommandPredicate predicate) {
		return (RecipientCommandBehavior) (from, to) -> predicate.test(from, to) ? reply : null;
	}

	public static Behavior recipientCommandRepliesIfNot(@NonNull Reply reply,
			@NonNull RecipientCommandPredicate predicate) {
		return (RecipientCommandBehavior) (from, to) -> predicate.test(from, to) ? null : reply;
	}

	public static Behavior dataCommandReplies(@NonNull Reply reply) {
		return (DataCommandBehavior) (from, to, data) -> reply;
	}

	public static Behavior dataCommandRepliesIf(@NonNull Reply reply, @NonNull DataCommandPredicate predicate) {
		return (DataCommandBehavior) (from, to, data) -> predicate.test(from, to, data) ? reply : null;
	}

	public static Behavior dataCommandRepliesIfNot(@NonNull Reply reply, @NonNull DataCommandPredicate predicate) {
		return (DataCommandBehavior) (from, to, data) -> predicate.test(from, to, data) ? null : reply;
	}

	public static Behavior authCommandReplies(@NonNull Reply reply) {
		return (AuthCommandBehavior) (input) -> reply;
	}

	public static Behavior authCommandRepliesIf(@NonNull Reply reply, AuthCommandPredicate predicate) {
		return (AuthCommandBehavior) (input) -> predicate.test(input) ? reply : null;
	}

	public static Behavior authCommandRepliesIfNot(@NonNull Reply reply, AuthCommandPredicate predicate) {
		return (AuthCommandBehavior) (input) -> predicate.test(input) ? null : reply;
	}

	@FunctionalInterface
	public static interface MailCommandPredicate extends Predicate<String> {
	}

	@FunctionalInterface
	public static interface RecipientCommandPredicate extends BiPredicate<String, String> {
	}

	@FunctionalInterface
	public static interface DataCommandPredicate {
		boolean test(String from, String to, InputStream data);
	}

	@FunctionalInterface
	public static interface AuthCommandPredicate extends Predicate<String> {
	}
}
