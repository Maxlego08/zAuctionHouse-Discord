package com.starrycity.zDiscord;

import java.util.UUID;

public class DiscordMessage {

	private final long channelID;
	private final long messageID;
	private final UUID uniqueId;
	private final long created_at;

	/**
	 * @param channelID
	 * @param messageID
	 * @param uniqueId
	 */
	public DiscordMessage(long channelID, long messageID, UUID uniqueId) {
		super();
		this.channelID = channelID;
		this.messageID = messageID;
		this.uniqueId = uniqueId;
		this.created_at = System.currentTimeMillis();
	}

	/**
	 * @return the channelID
	 */
	public long getChannelID() {
		return channelID;
	}

	/**
	 * @return the messageID
	 */
	public long getMessageID() {
		return messageID;
	}

	/**
	 * @return the uniqueId
	 */
	public UUID getUniqueId() {
		return uniqueId;
	}

	/**
	 * @return the created_at
	 */
	public long getCreated_at() {
		return created_at;
	}

}
