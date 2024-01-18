package com.zaitunlabs.zlcore.utils;

/*
 * Copyright (C) 2014 Alexrs95
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

/**
 * @author Alejandro Rodriguez <https://github.com/Alexrs95/Prefs>
 *         <p/>
 *         Wrapper over the Android Preferences which provides a fluid syntax
 */
public class Prefs {
	private static final String TAG = "Prefs";
	static Prefs singleton = null;
	static Prefs sharedSingleton = null;
	
	 SPrefs preferences;

	 SPrefs.Editor editor;

	 SharedPreferences sharedPreferences;
	 SharedPreferences.Editor sharedEditor;

	Prefs(Context context, boolean isSecure) {
		if(isSecure) {
			preferences = new SPrefs(context);
			editor = preferences.edit();
		} else {
			sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
			sharedEditor = sharedPreferences.edit();
		}
	}

	public static Prefs with(Context context) {
		return with(context, true);
	}

	public static Prefs with(Context context, boolean isSecure) {
		if(isSecure) {
			if (singleton == null) {
				singleton = new Builder(context, true).build();
			}
			singleton.forceSymetric();
			return singleton;
		} else {
			if (sharedSingleton == null) {
				sharedSingleton = new Builder(context, false).build();
			}
			return sharedSingleton;
		}
	}

	public Prefs forceSymetric(){
		preferences.forceSymetric();
		return this;
	}

	public void save(String key, boolean value) {
		if(editor != null) {
			editor.putBoolean(key, value).apply();
		}

		if(sharedEditor != null) {
			sharedEditor.putBoolean(key, value).apply();
		}

	}

	public void save(String key, String value) {
		if(editor != null) {
			editor.putString(key, value).apply();
		}

		if(sharedEditor != null) {
			sharedEditor.putString(key, value).apply();
		}
	}

	public void save(String key, int value) {
		if(editor != null) {
			editor.putInt(key, value).apply();
		}

		if(sharedEditor != null) {
			sharedEditor.putInt(key, value).apply();
		}
	}

	public void save(String key, float value) {
		if(editor != null) {
			editor.putFloat(key, value).apply();
		}

		if(sharedEditor != null) {
			sharedEditor.putFloat(key, value).apply();
		}
	}

	public void save(String key, long value) {
		if(editor != null) {
			editor.putLong(key, value).apply();
		}

		if(sharedEditor != null) {
			sharedEditor.putLong(key, value).apply();
		}
	}

	public void save(String key, Set<String> value) {
		if(editor != null) {
			editor.putStringSet(key, value).apply();
		}

		if(sharedEditor != null) {
			sharedEditor.putStringSet(key, value).apply();
		}
	}

	public boolean getBoolean(String key, boolean defValue) {
		if(preferences != null) {
			return preferences.getBoolean(key, defValue);
		} else {
			return sharedPreferences.getBoolean(key, defValue);
		}
	}

	public String getString(String key, String defValue) {
		if(preferences != null) {
			return preferences.getString(key, defValue);
		} else {
			return sharedPreferences.getString(key, defValue);
		}
	}

	public int getInt(String key, int defValue) {
		if(preferences != null) {
			return preferences.getInt(key, defValue);
		} else {
			return sharedPreferences.getInt(key, defValue);
		}
	}

	public float getFloat(String key, float defValue) {
		if(preferences != null) {
			return preferences.getFloat(key, defValue);
		} else {
			return sharedPreferences.getFloat(key, defValue);
		}
	}

	public long getLong(String key, long defValue) {
		if(preferences != null) {
			return preferences.getLong(key, defValue);
		} else {
			return sharedPreferences.getLong(key, defValue);
		}
	}

	public Set<String> getStringSet(String key, Set<String> defValue) {
		if(preferences != null) {
			return preferences.getStringSet(key, defValue);
		} else {
			return sharedPreferences.getStringSet(key, defValue);
		}
	}

	public Map<String, ?> getAll() {
		if(preferences != null) {
			return preferences.getAll();
		} else {
			return sharedPreferences.getAll();
		}
	}

	public void remove(String key) {
		if(editor != null) {
			editor.remove(key).apply();
		}

		if(sharedEditor != null) {
			sharedEditor.remove(key).apply();
		}

	}

	private static class Builder {
		private final Context context;
		private final boolean isSecure;

		public Builder(Context context, boolean isSecure) {
			if (context == null) {
				throw new IllegalArgumentException("Context must not be null.");
			}
			this.context = context.getApplicationContext();
			this.isSecure = isSecure;
		}


		/**
		 * Method that creates an instance of Prefs
		 * 
		 * @return an instance of Prefs
		 */
		public Prefs build() {
			return new Prefs(context, isSecure);
		}
	}
}