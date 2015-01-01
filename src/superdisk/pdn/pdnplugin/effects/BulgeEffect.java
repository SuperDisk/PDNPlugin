package superdisk.pdn.pdnplugin.effects;

import heroesgrave.spade.editing.Effect;
import heroesgrave.spade.gui.dialogs.GridEffectDialog;
import heroesgrave.spade.gui.misc.WeblafWrapper;
import heroesgrave.spade.image.Layer;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;

import superdisk.pdn.pdnplugin.changes.BulgeChange;

public class BulgeEffect //extends Effect
{
	/*//TODO: make this class work. I HATE GUI!
	private final SpinSlider xSS = new SpinSlider();
	private final SpinSlider ySS = new SpinSlider();
	private final SpinSlider amountSS = new SpinSlider();
	
	public BulgeEffect(String name)
	{
		super(name);
	}
	
	@Override
	public void perform(final Layer layer)
	{
		final GridEffectDialog dialog = new GridEffectDialog(1, 1, "Bulge", getIcon());
		
		final JSlider xSlider = xSS.getSlider();
		final JSlider ySlider = ySS.getSlider();
		final JSlider amountSlider = amountSS.getSlider();
		
		xSlider.setMaximum(1000);
		xSlider.setMinimum(-1000);
		xSlider.setValue(0);
		
		ySlider.setMaximum(1000);
		ySlider.setMinimum(-1000);
		ySlider.setValue(0);
		
		amountSlider.setMaximum(100);
		amountSlider.setMinimum(-200);
		amountSlider.setValue(45);
		
		{
			JPanel panel = dialog.getPanel(0);
			panel.setLayout(new GridLayout(4, 1));
			
			panel.add(xSS);
			panel.add(ySS);
			panel.add(amountSS);
		}
		
		JPanel panel = dialog.getBottomPanel();
		panel.setLayout(new GridLayout(0, 2));
		JButton create = WeblafWrapper.createButton("Apply");
		JButton cancel = WeblafWrapper.createButton("Cancel");
		
		panel.add(create);
		panel.add(cancel);
		
		create.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dialog.close();
				layer.addChange(new BulgeChange(amountSlider.getValue(), xSlider.getValue(), ySlider.getValue()));
			}
		});
		
		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dialog.close();
			}
		});
		
		dialog.display();
	}*/
}
