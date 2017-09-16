package net.hollowbit.archipeloshared;

public class Pair<E, O> {
	
	private E val1;
	private O val2;
	
	public Pair() {
	}
	
	public Pair(E val1, O val2) {
		this.val1 = val1;
		this.val2 = val2;
	}

	public E getVal1() {
		return val1;
	}

	public void setVal1(E val1) {
		this.val1 = val1;
	}

	public O getVal2() {
		return val2;
	}

	public void setVal2(O val2) {
		this.val2 = val2;
	}
	
}
