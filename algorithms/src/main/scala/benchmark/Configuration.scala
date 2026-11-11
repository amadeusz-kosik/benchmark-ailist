package benchmark

final case class Configuration(
    // Number of intervals being checked before deciding to either keep or discard the interval from
    //  the current component.
    intervalsCountToCheckLookahead: Int,
    // Number of intervals fully shadowed by the investigated interval during above check. Higher or equal number
    //  of shadowed intervals ejects the investigated interval from the component.
    intervalsCountToTriggerExtraction: Int,
    // Maximum number of intervals in the component. When met, creation of the new component is forced.
    maximumComponentSize: Int,
    // Minimum number of intervals needed to start extracting a component. If the number of available intervals is
    //  less than this value, all are used to form the component and shadowing checks are skipped.
    earlyStopLeftoverCount: Int
)

object Configuration {
    def apply(): Configuration =
        Configuration(24, 16, 100000, 1)
}

