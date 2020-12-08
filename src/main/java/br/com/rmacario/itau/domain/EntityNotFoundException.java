package br.com.rmacario.itau.domain;

/**
 * Exceção abstrata que visa facilitar a manipulação de exceções lançadas quando a busca por uma
 * entidade não retorna registros.
 *
 * @author rmacario
 */
abstract class EntityNotFoundException extends RuntimeException {}
