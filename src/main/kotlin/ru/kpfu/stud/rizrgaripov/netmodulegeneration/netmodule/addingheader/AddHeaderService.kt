package ru.kpfu.stud.rizrgaripov.netmodulegeneration.netmodule.addingheader

import com.intellij.openapi.components.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Service
@State(name = "AddHeaderData", storages = [Storage("AddHeaderData.xml")])
class AddHeaderService : PersistentStateComponent<AddHeaderService.State> {
    companion object {
        val instance: AddHeaderService
            get() = ServiceManager.getService(AddHeaderService::class.java)
    }

    var addHeaderState = State()

    override fun getState(): State {
        return addHeaderState
    }

    override fun loadState(stateLoadedFromPersistence: State) {
        addHeaderState = stateLoadedFromPersistence
    }

    class State {
        var headerName: String by object : LoggingProperty<State, String>("") {}
        var headerValue: String by object : LoggingProperty<State, String>("") {}

        open class LoggingProperty<R, T>(initValue: T) : ReadWriteProperty<R, T> {
            private var backingField: T = initValue

            override operator fun getValue(thisRef: R, property: KProperty<*>): T {
                return backingField
            }

            override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
                backingField = value
            }
        }
    }
}