package net.hollowbit.archipelo.screen.screens.playercreator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.tools.LM;

public class ColorPicker extends Window {
	
	int selectedColor;
	
	Table colorTable;
	ButtonGroup<Button> colorButtons;
	
	public ColorPicker (String nameOfThingColorIsFor, int currentlySelected, Color[] colors, final ColorPickListener listener) {
		super(LM.ui("pick") + " " + nameOfThingColorIsFor + " " + LM.ui("color"), ArchipeloClient.getGame().getUiSkin());

		colorTable = new Table(getSkin());
		colorButtons = new ButtonGroup<Button>();
		
		//Loop through each color and add a button for it
		for (int i = 0; i < colors.length; i++) {
			final int index = i;
			
			NinePatch patch = new NinePatch(ArchipeloClient.getGame().getAssetManager().getTexture("blank"));
			patch.setColor(colors[i]);
			NinePatchDrawable drawablePatch = new NinePatchDrawable(patch);
			
			NinePatch patchSelected = new NinePatch(ArchipeloClient.getGame().getAssetManager().getTexture("blank-border"));
			patchSelected.setColor(colors[i]);
			NinePatchDrawable drawablePatchSelected = new NinePatchDrawable(patchSelected);
			
			ButtonStyle style = new ButtonStyle(drawablePatch, drawablePatchSelected, drawablePatchSelected);
			Button button = new Button(style);
			button.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					selectedColor = index;
					super.clicked(event, x, y);
				}
			});
			
			if (i == currentlySelected) {//If this color is selected already, choose it by default
				selectedColor = i;
				button.setChecked(true);
			}

			if (i % 5 == 0)//Every 4 colors, start a new row
				colorTable.row();
			
			colorTable.add(button).width(50).height(50);
			colorButtons.add(button);
		}
		add(colorTable).colspan(2).pad(3);
		
		row();
		
		//Cancel button to cancel color selection
		TextButton cancelButton = new TextButton(LM.ui("cancel"), getSkin());
		cancelButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				remove();
				super.clicked(event, x, y);
			}
		});
		add(cancelButton).pad(3);
		
		//Ok button to confirm color selection
		TextButton okButton = new TextButton(LM.ui("ok"), getSkin());
		okButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				listener.colorPicked(selectedColor);
				remove();
				super.clicked(event, x, y);
			}
		});
		add(okButton).pad(3);
		
		pack();
	}
	
}
