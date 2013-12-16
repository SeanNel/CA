package graphics.filters;

import std.Picture;

/**
 * Non-CA image filter.
 * 
 * @author Sean
 */
public abstract class Filter {
	/**
	 * Applies the filter to the specified image.
	 * 
	 * @param picture Image to apply the filter to.
	 * @return Filtered image.
	 */
	public static Picture apply(Picture picture) {
		return picture;
	}
}
