package com.starrycity.zDiscord.zcore.utils;
public class Color {

	private final int r;
	private final int g;
	private final int b;

	public final transient String USER = "%%__USER__%%";
	
	public Color(int r, int g, int b) {
		super();
		this.r = r <= 255 ? r : 255;
		this.g = g <= 255 ? g : 255;
		this.b = b <= 255 ? b : 255;
	}

	public java.awt.Color color() {
		return new java.awt.Color(r, g, b);
	}

	/**
	 * @return the r
	 */
	public int getR() {
		return r;
	}

	/**
	 * @return the g
	 */
	public int getG() {
		return g;
	}

	/**
	 * @return the b
	 */
	public int getB() {
		return b;
	}

}