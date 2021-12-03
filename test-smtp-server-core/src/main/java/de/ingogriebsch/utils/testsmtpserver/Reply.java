package de.ingogriebsch.utils.testsmtpserver;

import static de.ingogriebsch.utils.testsmtpserver.ReplyCode.ACTION_OK;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder
@Value
public final class Reply {
	@NonNull
	ReplyCode code;
	String message;

	public static Reply success() {
		return Reply.builder().code(ACTION_OK).message("OK").build();
	}
}
