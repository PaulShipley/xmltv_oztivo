package name.paulshipley.xmltv;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

/**
 * The Class Channel <br/>
 * 
 * Object to represent each of the channels.
 * 
 * @author Paul Shipley (pshipley@melbpc.org.au)
 * @version $Id: Channel.java,v 1.6 2010/01/05 12:25:51 paul Exp $
 */
public class Channel {
	private String id;
	private String display_name;
	private Boolean selected;

	private Channel outer;

	/**
	 * Instantiates a new channel.
	 */
	public Channel() {
		super();
		this.outer = this;

		this.id = null;
		this.display_name = "";
		this.selected = false;
	}

	/**
	 * Instantiates a new channel.
	 * 
	 * @param id
	 *            the channel id
	 * @param display_name
	 *            the channel display_name
	 */
	public Channel(String id, String display_name) {
		super();
		this.outer = this;

		this.id = id;
		this.display_name = display_name;
		this.selected = false;
	}

	/**
	 * Gets the channel id.
	 * 
	 * @return the channel id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the channel id.
	 * 
	 * @param id
	 *            the new channel id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the channel display name.
	 * 
	 * @return the channel display name
	 */
	public String getDisplay_name() {
		return display_name;
	}

	/**
	 * Sets the channel display _name.
	 * 
	 * @param displayName
	 *            the new channel display name
	 */
	public void setDisplay_name(String displayName) {
		display_name = displayName;
	}

	/**
	 * Is the channel selected.
	 * 
	 * @return is selected
	 */
	public Boolean getSelected() {
		return selected;
	}

	/**
	 * Sets the channel selected value.
	 * 
	 * @param selected
	 *            the new channel selected value
	 */
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Channel [display_name=" + display_name + ", id=" + id
				+ ", selected=" + selected + "]";
	}

	/**
	 * Display this channel.
	 * 
	 * @return the channel as a GUI component
	 */
	public JPanel display() {
		final ChannelVisual p = new ChannelVisual();

		// configure label for display name
		final Font labelPlain = p.getJLabel().getFont();
		final Font labelBold = labelPlain.deriveFont(Font.BOLD);
		p.getJLabel().setText(this.display_name);
		if (this.selected) {
			p.getJLabel().setFont(labelBold);
		} else {
			p.getJLabel().setFont(labelPlain);
		}

		// configure checkbox for selected
		p.getJCheckBox().setSelected(this.selected);
		p.getJCheckBox().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				outer.setSelected(p.getJCheckBox().isSelected());
				if (outer.getSelected()) {
					p.getJLabel().setFont(labelBold);
				} else {
					p.getJLabel().setFont(labelPlain);
				}
				// Debug.println("Selected:" + outer.toString());
			}
		});

		return p;
	}
}
