package ch.sourcemotion.vertx.kotlin.eventbus.browser.core


typealias ReconnectListener = () -> Unit
typealias ErrorListener = (BusError) -> Unit
typealias CloseListener = (dynamic) -> Unit

typealias Consumer<V> = (Message<V>) -> Unit

typealias Address = String
typealias MessageHeaders = Map<String, String>
