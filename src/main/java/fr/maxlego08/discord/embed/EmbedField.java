package fr.maxlego08.discord.embed;

public class EmbedField {

	private final String tile;
	private final String message;
	private final boolean inLine;
	private boolean displayWhenEnchantIsNull = true;

	public EmbedField(String tile, String message, boolean inLine, boolean displayWhenEnchantIsNull) {
		this.tile = tile;
		this.message = message;
		this.inLine = inLine;
		this.displayWhenEnchantIsNull = displayWhenEnchantIsNull;
	}

	public EmbedField(String tile, String message, boolean inLine) {
		this.tile = tile;
		this.message = message;
		this.inLine = inLine;
	}

	public String getTile() {
		return tile;
	}

	public String getMessage() {
		return message;
	}

	public boolean isInLine() {
		return inLine;
	}

	public boolean displayWhenEnchantIsNull() {
		return displayWhenEnchantIsNull;
	}

}
