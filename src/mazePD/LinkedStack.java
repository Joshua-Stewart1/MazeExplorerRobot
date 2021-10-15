package mazePD;

public class LinkedStack<E> implements Stack<E>
{
	private SinglyLinkedList<E> list = new SinglyLinkedList<>();
	public LinkedStack() {};
	public int size()
	{
		return list.size();
	}
	public Boolean isEmpty()
	{
		return list.isEmpty();
	}
	public void push(E element)
	{
		list.addFirst(element);
	}
	public E top()
	{
		return list.first();
	}
	public E pop()
	{
		return list.removeFirst();
	}
}
