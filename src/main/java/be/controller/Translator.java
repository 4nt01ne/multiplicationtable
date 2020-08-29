package be.controller;
import java.util.Locale;
import java.util.ResourceBundle;

public class Translator {
  private Locale currentLocale = new Locale("nl", "BE");
  private ResourceBundle messages = ResourceBundle.getBundle("Messages", currentLocale);

  public Translator() {
  }

  public Translator(String languageIso2, String countryIso3) {
    if(languageIso2 != null && countryIso3 != null) {
      currentLocale = new Locale(String.valueOf(languageIso2.toLowerCase()), countryIso3.toUpperCase());
      messages = ResourceBundle.getBundle("Messages", currentLocale);
    }
  }

  public String say(String property) {
    return messages.getString(property);
  }
}