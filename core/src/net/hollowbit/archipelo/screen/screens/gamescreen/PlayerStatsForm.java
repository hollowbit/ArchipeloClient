package net.hollowbit.archipelo.screen.screens.gamescreen;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.hollowbit.archipelo.entity.living.Player;
import net.hollowbit.archipelo.form.Form;
import net.hollowbit.archipelo.items.Item;
import net.hollowbit.archipelo.screen.screens.mainmenu.CharacterDisplay;
import net.hollowbit.archipelo.tools.LM;
import net.hollowbit.archipelo.tools.QuickUi;
import net.hollowbit.archipeloshared.FormData;

public class PlayerStatsForm extends Form {
	
	private static final String SPEED = "speed";
	private static final String MIN_DAMAGE = "minDamage";
	private static final String MAX_DAMAGE = "maxDamage";
	private static final String DEFENSE = "defense";
	private static final String DAMAGE_MULTIPLIER = "damageMultiplier";
	private static final String DEFENSE_MULTIPLIER = "defenseMultiplier";
	private static final String CRIT_MULTIPLIER = "critMultiplier";
	private static final String CRIT_CHANCE = "critChance";
	
	private static final int DISPLAY_SIZE = 150;
	
	private CharacterDisplay characterDisplay;
	private Table statsTable;
	
	private Label speed;
	private Label minDamage;
	private Label maxDamage;
	private Label defense;
	private Label damageMultiplier;
	private Label defenseMultiplier;
	private Label critMultiplier;
	private Label critChance;
	
	private Label speedStat;
	private Label minDamageStat;
	private Label maxDamageStat;
	private Label defenseStat;
	private Label damageMultiplierStat;
	private Label defenseMultiplierStat;
	private Label critMultiplierStat;
	private Label critChanceStat;
	
	public PlayerStatsForm () {
		super(LM.ui("myStats"));
		
		this.setResizable(false);
		this.setMovable(true);
		this.setSize(400, 400);
		QuickUi.addCloseButtonToWindow(this);
		
		characterDisplay = new CharacterDisplay(new Item[Player.EQUIP_SIZE], true);
		this.add(characterDisplay).width(DISPLAY_SIZE).height(DISPLAY_SIZE).padLeft(45).padRight(10);
		
		statsTable = new Table(getSkin());
		
		speed = new Label(LM.ui("speed") + ": ", getSkin());
		speedStat = new Label("", getSkin());
		statsTable.add(speed).growX().left();
		statsTable.add(speedStat).growX().left();
		statsTable.row();
		
		minDamage = new Label(LM.ui("minDamage") + ": ", getSkin());
		minDamageStat = new Label("", getSkin());
		statsTable.add(minDamage).growX().left();
		statsTable.add(minDamageStat).growX().left();
		statsTable.row();
		
		maxDamage = new Label(LM.ui("maxDamage") + ": ", getSkin());
		maxDamageStat = new Label("", getSkin());
		statsTable.add(maxDamage).growX().left();
		statsTable.add(maxDamageStat).growX().left();
		statsTable.row();
		
		defense = new Label(LM.ui("defense") + ": ", getSkin());
		defenseStat = new Label("", getSkin());
		statsTable.add(defense).growX().left();
		statsTable.add(defenseStat).growX().left();
		statsTable.row();
		
		damageMultiplier = new Label(LM.ui("damageMultiplier") + ": ", getSkin());
		damageMultiplierStat = new Label("", getSkin());
		statsTable.add(damageMultiplier).growX().left();
		statsTable.add(damageMultiplierStat).growX().left();
		statsTable.row();
		
		defenseMultiplier = new Label(LM.ui("defenseMultiplier") + ": ", getSkin());
		defenseMultiplierStat = new Label("", getSkin());
		statsTable.add(defenseMultiplier).growX().left();
		statsTable.add(defenseMultiplierStat).growX().left();
		statsTable.row();
		
		critMultiplier = new Label(LM.ui("critMultiplier") + ": ", getSkin());
		critMultiplierStat = new Label("", getSkin());
		statsTable.add(critMultiplier).growX().left();
		statsTable.add(critMultiplierStat).growX().left();
		statsTable.row();
		
		critChance = new Label(LM.ui("critChance") + ": ", getSkin());
		critChanceStat = new Label("", getSkin());
		statsTable.add(critChance).growX().left();
		statsTable.add(critChanceStat).growX().left();
		
		statsTable.pack();
		this.add(statsTable).padLeft(10).padRight(45);
		
		this.pack();
	}
	
	@Override
	public void act(float delta) {
		if (gameScreen.getWorld() != null && gameScreen.getWorld().getPlayer() != null)
			characterDisplay.setEquippedInventory(gameScreen.getWorld().getPlayer().getDisplayInventory());
		super.act(delta);
	}

	@Override
	public void update (FormData formData) {
		this.speedStat.setText(formData.data.get(SPEED));
		this.minDamageStat.setText(formData.data.get(MIN_DAMAGE));
		this.maxDamageStat.setText(formData.data.get(MAX_DAMAGE));
		this.defenseStat.setText(formData.data.get(DEFENSE));
		this.damageMultiplierStat.setText(formData.data.get(DAMAGE_MULTIPLIER));
		this.defenseMultiplierStat.setText(formData.data.get(DEFENSE_MULTIPLIER));
		this.critMultiplierStat.setText(formData.data.get(CRIT_MULTIPLIER));
		this.critChanceStat.setText(formData.data.get(CRIT_CHANCE));
	}

}
