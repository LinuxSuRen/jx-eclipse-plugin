/**
 * 
 */
package io.jenkins.x.eclipse.plugin.view;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import io.jenkins.x.client.tree.BaseNode;
import io.jenkins.x.client.tree.TreeItem;

/**
 * @author suren
 */
class ViewLabelProvider extends StyledCellLabelProvider {
	private Map<String, Image> imageCache = new HashMap<String, Image>();

	@Override
	public void update(ViewerCell cell) {
		Object obj = cell.getElement();
		StyledString styledString = new StyledString(getText(obj));

		cell.setText(styledString.toString());
		cell.setStyleRanges(styledString.getStyleRanges());
		cell.setImage(getImage(obj));
		
		super.update(cell);
	}

	private String getText(Object obj) {
		if(obj instanceof TreeItem) {
			return ((TreeItem) obj).getLabel();
		}
		return obj.toString();
	}
	
	private Image getImage(Object obj) {
		String imagePath = null;
		if(obj instanceof BaseNode) {
			imagePath = ((TreeItem) obj).getIconPath();
		}
		
		Image image = null;
		if(imagePath != null && !"".equals(imagePath)) {
			image = imageCache.get(imagePath);
			
			if(image == null) {
				ClassLoader loader = getClass().getClassLoader();
				InputStream imageInput = loader.getResourceAsStream(imagePath);
				if(imageInput != null) {
					image = new Image(Display.getCurrent(), imageInput);
					image = new Image(Display.getCurrent(), image.getImageData().scaledTo(20, 20));
					imageCache.put(imagePath, image);
				}
			}
		}
		
		return image;
	}
}
