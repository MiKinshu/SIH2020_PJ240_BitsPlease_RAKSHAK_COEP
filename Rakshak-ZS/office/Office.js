var mongoose = require("mongoose");
var NetworkSchema = new mongoose.Schema({
    officeName: {
        type: String,
        default: ""
    },
    password: {
        type: String,
        default: ""
    },
    officeId: {
        type: String,
        default: ""
    },
    officers: {
        type: [String],
        default: []
    },
    Type: {
        type: Number,
        default: 0
    },
    location: {
        type: String,
        default: ""
    }

});
mongoose.model("Office", NetworkSchema);

module.exports = mongoose.model("Office");