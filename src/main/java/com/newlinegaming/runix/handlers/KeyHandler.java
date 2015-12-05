package com.newlinegaming.runix.handlers;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;


public class KeyHandler {
	
	@SubscribeEvent
	public void onKeyinput(InputEvent.KeyInputEvent event) {
		if(KeyBindings.CHARGE.isPressed()) {
			
		}
	}
	
	public static class KeyBindings {
		
		public static KeyBinding CHARGE;
		
		public void init() {
			
			CHARGE = new KeyBinding("key.transmode", Keyboard.KEY_C, "key.catergories.runix");
			
		}
		
	}

}
