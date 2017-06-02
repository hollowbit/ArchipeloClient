package net.hollowbit.archipeloshared.rangeindexer;

/**
 * Created by Nathanael on 6/2/2017.
 */
public class IllegalRangeMinimumException extends IllegalArgumentException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IllegalRangeMinimumException() {
        super("The specified range minimum is illegal. It must be less than the first range limit.");
    }

}
