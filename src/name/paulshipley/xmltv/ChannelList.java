package name.paulshipley.xmltv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import name.paulshipley.Common.ExceptionHandler;

/**
 * The Class ChannelList <br/>
 * Implements a collection of Channel objects so as to hide the underlying
 * implementation.
 * 
 * @author Paul Shipley (pshipley@melbpc.org.au)
 * @version $Id: ChannelList.java,v 1.4 2010/03/23 08:54:15 paul Exp $
 */
public class ChannelList implements Collection<Channel> {
	private HashMap<String, Channel> ChList;

	/**
	 * Instantiates a new channel list.
	 */
	public ChannelList() {
		super();

		this.ChList = new HashMap<String, Channel>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(Channel ch) {
		boolean saved = false;

		assert ch != null : "channel is null";

		if (!this.contains(ch)) {
			this.ChList.put(ch.getId(), ch);
			saved = true;
		}
		return saved;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends Channel> c) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#clear()
	 */
	public void clear() {
		this.ChList.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		assert o != null : "channel is null";

		return this.ChList.containsKey(((Channel) o).getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty() {
		return this.ChList.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		assert o != null : "channel is null";

		this.ChList.remove(((Channel) o).getId());
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#size()
	 */
	public int size() {
		return this.ChList.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray() {
		Collection<Channel> chl = this.ChList.values();
		return chl.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#toArray(T[])
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		Collection<Channel> chl = this.ChList.values();
		return (T[]) chl.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#iterator()
	 */
	public Iterator<Channel> iterator() {
		Collection<Channel> chcoll = this.ChList.values();
		List<Channel> chl = new ArrayList<Channel>(chcoll);

		// sort by Selected, Display Name
		Collections.sort(chl, new Comparator<Channel>() {
			public int compare(Channel ch1, Channel ch2) {
				int result;

				if (ch1.getSelected() & !ch2.getSelected()) {
					result = -1;
				}
				else if (!ch1.getSelected() & ch2.getSelected()) {
					result = 1;
				}
				else {
					result = ch1.getDisplay_name().compareTo(ch2.getDisplay_name());
				}
					
				return result;
			}
		});

		Iterator<Channel> chlit = chl.iterator();
		return chlit;
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		try {
			ChannelList chl = new ChannelList();

			System.out.println("isEmpty=" + Boolean.toString(chl.isEmpty()));
			System.out.println("Size=" + chl.size());

			chl.add(new Channel("id1", "name1"));
			chl.add(new Channel("id2", "name2"));

			System.out.println("isEmpty=" + Boolean.toString(chl.isEmpty()));
			System.out.println("Size=" + chl.size());

			Iterator<Channel> chit = chl.iterator();
			while (chit.hasNext()) {
				System.out.println(chit.next().toString());
			}

		} catch (Exception e) {
			ExceptionHandler.handleAndTerminate(e);
		}

		System.exit(0);
	}
}
