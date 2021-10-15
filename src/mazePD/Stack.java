package mazePD;

public interface Stack<E>
{
	int size();
	Boolean isEmpty();
	void push(E e);
	E top();
	E pop();
}
