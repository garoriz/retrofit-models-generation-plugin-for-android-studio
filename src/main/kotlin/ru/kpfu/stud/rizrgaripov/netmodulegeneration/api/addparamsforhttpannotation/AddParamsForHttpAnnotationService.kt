package ru.kpfu.stud.rizrgaripov.netmodulegeneration.api.addparamsforhttpannotation

import com.intellij.openapi.components.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Service
@State(name = "AddParamsForHttpAnnotationData", storages = [Storage("AddParamsForHttpAnnotationData.xml")])
class AddParamsForHttpAnnotationService : PersistentStateComponent<AddParamsForHttpAnnotationService.State> {
    companion object {
        val instance: AddParamsForHttpAnnotationService
            get() = ServiceManager.getService(AddParamsForHttpAnnotationService::class.java)
    }

    var addParamsForHttpAnnotationState = State()

    override fun getState(): State {
        return addParamsForHttpAnnotationState
    }

    override fun loadState(stateLoadedFromPersistence: State) {
        addParamsForHttpAnnotationState = stateLoadedFromPersistence
    }

    class State {
        var method: String by object : LoggingProperty<State, String>("") {}
        var hasBody: Boolean by object : LoggingProperty<State, Boolean>(false) {}

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