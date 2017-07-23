package name.paulshipley.xmltv;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The Class ChannelVisual <br/>
 * GUI template to display a Channel object
 * 
 * @author Paul Shipley (pshipley@melbpc.org.au)
 * @version $Id: ChannelVisual.java,v 1.1 2009/12/26 12:33:43 paul Exp $
 */
public class ChannelVisual extends JPanel {

	private static final long serialVersionUID = 1L;
	private JCheckBox jCheckBox = null;
	private JLabel jLabel = null;

	/**
	 * This is the default constructor
	 */
	public ChannelVisual() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		jLabel = new JLabel();
		jLabel.setText("JLabel");
		jLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		jLabel.setPreferredSize(new Dimension(400, 21));
		this.setSize(450, 30);
		this.setLayout(new FlowLayout());
		this.setPreferredSize(new Dimension(450, 30));
		this.add(getJCheckBox(), null);
		this.add(jLabel, null);
	}

	/**
	 * This method initializes jCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	public JCheckBox getJCheckBox() {
		if (jCheckBox == null) {
			jCheckBox = new JCheckBox();
		}
		return jCheckBox;
	}

	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	public JLabel getJLabel() {
		return jLabel;
	}

}
