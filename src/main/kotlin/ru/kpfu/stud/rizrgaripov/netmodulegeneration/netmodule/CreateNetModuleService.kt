package ru.kpfu.stud.rizrgaripov.netmodulegeneration.netmodule

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


@Service
@State(name = "CreateNetModuleData", storages = [Storage("CreateNetModuleData.xml")])
class CreateNetModuleService : PersistentStateComponent<CreateNetModuleService.State> {
    companion object {
        val instance: CreateNetModuleService
            get() = getService(CreateNetModuleService::class.java)
    }

    var createNetModuleState = State()

    override fun getState(): State {
        return createNetModuleState
    }

    override fun loadState(stateLoadedFromPersistence: State) {
        createNetModuleState = stateLoadedFromPersistence
    }

    class State {
        var baseUrl: String by object : LoggingProperty<State, String>("") {}
        var isSupportRxJava: Boolean by object : LoggingProperty<State, Boolean>(false) {}
        var apiClassName: String by object : LoggingProperty<State, String>("") {}

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

