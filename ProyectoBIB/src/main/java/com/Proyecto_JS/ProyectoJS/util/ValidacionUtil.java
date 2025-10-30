package com.Proyecto_JS.ProyectoJS.util;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.commons.lang3.StringUtils;

public class ValidacionUtil {

  public static void validarEmail(String email) {
    checkArgument(StringUtils.isNotBlank(email), "Email vacío");
    checkArgument(StringUtils.contains(email, "@"), "Email inválido");
  }

  public static void validarNoVacio(String valor, String nombreCampo) {
    checkArgument(StringUtils.isNotBlank(valor), nombreCampo + " no puede estar vacío");
  }
}
