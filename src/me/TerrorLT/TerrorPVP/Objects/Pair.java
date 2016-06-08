package me.TerrorLT.TerrorPVP.Objects;

public class Pair<L, R> {
	
	L left_data;
	R right_data;
	
	public Pair(L left, R right)
	{
		left_data = left;
		right_data = right;
	}
	
	public L getLeft()
	{
		return left_data;
	}
	
	public R getRight()
	{
		return right_data;
	}
	
	public void setLeft(L left)
	{
		left_data = left;
	}
	
	public void setRight(R right)
	{
		right_data = right;
	}
	
}
