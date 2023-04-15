package ru.kpfu.stud.rizrgaripov.netmodulegeneration.api.addparam

import com.intellij.openapi.components.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Service
@State(name = "AddParamData", storages = [Storage("AddParamData.xml")])
class AddParamService : PersistentStateComponent<AddParamService.State> {
    companion object {
        val instance: AddParamService
            get() = ServiceManager.getService(AddParamService::class.java)
    }

    var addParamState = State()

    override fun getState(): State {
        return addParamState
    }

    override fun loadState(stateLoadedFromPersistence: State) {
        addParamState = stateLoadedFromPersistence
    }

    class State {
        var annotationType: AnnotationType by object : LoggingProperty<State, AnnotationType>(AnnotationType.QUERY) {}
        var annotationValue: String by object : LoggingProperty<State, String>("") {}
        var paramName: String by object : LoggingProperty<State, String>("") {}
        var paramDataType: String by object : LoggingProperty<State, String>("") {}

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

    enum class AnnotationType(val string: String) {
        QUERY("retrofit2.http.Query"),
        BODY("retrofit2.http.Body"),
        FIELD("retrofit2.http.Field"),
        FIELD_MAP("retrofit2.http.FieldMap"),
        HEADER("retrofit2.http.Header"),
        PART("retrofit2.http.Part"),
        PART_MAP("retrofit2.http.PartMap"),
        PATH("retrofit2.http.Path"),
        QUERY_MAP("retrofit2.http.QueryMap"),
        QUERY_NAME("retrofit2.http.QueryName"),
        TAG("retrofit2.http.Tag"),
        URL("retrofit2.http.Url"),
    }
}