Vue.createApp({
    data() {
        return {
            clientInfo: {},
            creditCards: [],
            debitCards: [],
            errorToats: null,
            errorMsg: null
        }
    },
    methods: {
        getData: function () {
            axios.get("/api/clients/current")
                .then((response) => {
                    //get client ifo
                    this.clientInfo = response.data;
                    this.creditCards = this.clientInfo.cards.filter(card => card.type == "CREDIT");
                    this.debitCards = this.clientInfo.cards.filter(card => card.type == "DEBIT");
                })
                .catch((error) => {
                    this.errorMsg = "Error getting data";
                    this.errorToats.show();
                })
        },
        formatDate: function (date) {
            return new Date(date).toLocaleDateString('en-gb');
        },
        signOut: function () {
            axios.post('/api/logout')
                .then(response => window.location.href = "/web/index.html")
                .catch(() => {
                    this.errorMsg = "Sign out failed"
                    this.errorToats.show();
                })
        },
        deleteCard: function (id) {
             const isConfirmed = confirm("Are you sure you want to delete the card?");

              if (isConfirmed) {
                    axios.delete('/api/clients/current/card/' + id)
                        .then((response) => {
                             this.getData();
                        })
                        .catch((error) => {
                            this.errorMsg = response.statusText;
                            this.errorToats.show();
                        });
              }
        },
        isExpired: function(thruDate) {
            const currentDate = new Date();
            const expirationDate = new Date(thruDate);
            return expirationDate < currentDate;
          },
    },
    mounted: function () {
        this.errorToats = new bootstrap.Toast(document.getElementById('danger-toast'));
        this.okmodal = new bootstrap.Modal(document.getElementById('okModal'));
        this.getData();
    }
}).mount('#app')