package wario.arekp.pl;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class MyPreferences extends PreferenceActivity {
	private static final String PREFERENCES_NAME = "Preferences";
	private static final String CHECKBOX_FIELD = "lcdOFF";
	private static final String LIST_FIELD = "list";
	
	private SharedPreferences preferences;
	private CheckBoxPreference lcdOFF;
	private EditTextPreference edittextOpadanie;
	private EditTextPreference edittextWznoszenie;
	private EditTextPreference cisnieniewzorcowe;
	private ListPreference listPreference;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.mypreferences);
		preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
		lcdOFF = (CheckBoxPreference) findPreference("lcdOFF");
		edittextOpadanie = (EditTextPreference) findPreference("edittextOpadanie");
		edittextWznoszenie= (EditTextPreference) findPreference("edittextWznoszenie");
		cisnieniewzorcowe = (EditTextPreference) findPreference("cisnieniewzorcowe");
		listPreference = (ListPreference) findPreference("list");
		initPreferences();
	}

	private void initPreferences() {
		boolean checkBoxValue = preferences.getBoolean(CHECKBOX_FIELD, false);
		String edittextOpadanieValue = preferences.getString("edittextOpadanie", "");
		String edittextWznoszenieValue = preferences.getString("edittextWznoszenie", "");
		String cisnieniewzorcoweValue = preferences.getString("cisnieniewzorcowe", "");
		String listDefaultValue = listPreference.getEntryValues()[0].toString();
		String listValue = preferences.getString(LIST_FIELD, listDefaultValue);
		
		lcdOFF.setChecked(checkBoxValue);
		edittextOpadanie.setText(edittextOpadanieValue);
		edittextWznoszenie.setText(edittextWznoszenieValue);
		cisnieniewzorcowe.setText(cisnieniewzorcoweValue);
		listPreference.setValue(listValue);		
	}

	@Override
	protected void onPause() {
		super.onPause();
		savePreferences();
	}

	private void savePreferences() {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(CHECKBOX_FIELD, lcdOFF.isChecked());
		editor.putString("edittextOpadanie", edittextOpadanie.getText());
		editor.putString("edittextWznoszenie", edittextWznoszenie.getText());
		editor.putString("cisnieniewzorcowe", cisnieniewzorcowe.getText());
		editor.putString(LIST_FIELD, listPreference.getValue());
		editor.commit();
	}
}
