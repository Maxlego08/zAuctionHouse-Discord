package fr.maxlego08.discord.action;

import fr.maxlego08.discord.embed.EmbedField;
import fr.maxlego08.discord.zcore.utils.Color;

import java.util.List;

public class Action {

    private final String header;
    private final String footer;
    private final Color embedColor;
    private final List<EmbedField> embedFields;

    public Action(String header, String footer, Color embedColor, List<EmbedField> embedFields) {
        this.header = header;
        this.footer = footer;
        this.embedColor = embedColor;
        this.embedFields = embedFields;
    }

    public String getHeader() {
        return header;
    }

    public String getFooter() {
        return footer;
    }

    public Color getEmbedColor() {
        return embedColor;
    }

    public List<EmbedField> getEmbedFields() {
        return embedFields;
    }
}
