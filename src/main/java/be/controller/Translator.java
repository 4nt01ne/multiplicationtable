package be.controller;
import java.util.Locale;
import java.util.ResourceBundle;

public class Translator {
  private Locale currentLocale = new Locale("nl", "BE");
  private ResourceBundle messages = ResourceBundle.getBundle("Messages", currentLocale);

  public Translator(String[] args) {
    if (args.length == 2) {
      currentLocale = new Locale(String.valueOf(args[0]), String.valueOf(args[1]));
      messages = ResourceBundle.getBundle("Messages", currentLocale);
    }
  }

  public String say(String property) {
    return messages.getString(property);
  }
}