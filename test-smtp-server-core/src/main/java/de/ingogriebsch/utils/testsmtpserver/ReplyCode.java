package de.ingogriebsch.utils.testsmtpserver;

import lombok.Getter;

@Getter
public enum ReplyCode {

	ACTION_OK(250, "Requested mail action okay, completed"), //
	SERVICE_NOT_AVAILABLE(421, "Service not available");

	private final Integer value;
	private final String text;

	private ReplyCode(Integer value, String text) {
		this.value = value;
		this.text = text;
	}
}
